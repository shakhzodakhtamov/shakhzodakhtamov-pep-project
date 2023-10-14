package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

/**
 * TO DO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    public SocialMediaController(){
        accountService = new AccountService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        //app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::postHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);
        return app;
    }
    //this handler is used to create a new account.
    private void postHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper= new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        //Account createdAccount= accountService.addAccount(account);
        if (account.username.isEmpty() || account.password.length() < 4 || accountService.usernameAlreadyExists(account.username)){
            ctx.status(400);
        }else{
            Account createdAccount= accountService.addAccount(account);
            ctx.json(mapper.writeValueAsString(createdAccount));
        }
    } 
    private void loginHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account login = accountService.loginAccount(account);
        if(login==null){
            ctx.status(401);
        }else{
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(login));
        }
    }    
    private void createMessageHandler (Context ctx) throws JsonProcessingException{
        ObjectMapper mapper= new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);

        if(message.message_text.isEmpty()||message.message_text.length()>254 || accountService.getUserById(message.getPosted_by())==null){//||message.posted_by()
            ctx.status(400);
        }else{        
            Message createdMessage= accountService.addMessage(message);
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(createdMessage));
        }
    }
    private void getAllMessagesHandler(Context ctx){
        ctx.status(200);
        ctx.json(accountService.getAllMessages());
    }
    /*private void getMessageByIdHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper= new ObjectMapper();
        Message messageId = mapper.readValue(ctx.body(), Message.class);
        Message messageById = accountService.getMessageById(messageId);
        ctx.json(mapper.writeValueAsString(messageById)).status(200);
    }
    private void getMessageByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message messageById = accountService.getMessageById(new Message(message_id, 0, "", 0)); 
        if (messageById != null) {
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(messageById));
        }
    }*/
    private void getMessageByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String message_id = ctx.pathParam("message_id");
        
        try {
            int messageId = Integer.parseInt(message_id);
            Message messageById = accountService.getMessageById(messageId);
    
            if (messageById != null) {
                ctx.status(200);
                ctx.json(mapper.writeValueAsString(messageById));
            } else {
                ctx.status(200);  // Message did not exist
            }
        } catch (NumberFormatException e) {
            ctx.status(400);  // Invalid message ID
        }
    }
    private void deleteMessageHandler(Context ctx) {
        String messageIdParam = ctx.pathParam("message_id");
        Integer messageId = null;
    
        try {
            messageId = Integer.parseInt(messageIdParam);
        } catch (NumberFormatException e) {
            ctx.status(400);
            return;
        }
    
        Message deletedMessage = accountService.deleteMessageById(messageId);
    
        if (deletedMessage != null) {
            ctx.json(deletedMessage);
            ctx.status(200);  // Message was deleted successfully
        } else {
            ctx.status(200);  // Message did not exist
        }
    }
    private void updateMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        String newMessageText = ctx.formParam("message_text");
    
        if (newMessageText != null && !newMessageText.isEmpty() && newMessageText.length() <= 255) {
            boolean updated = accountService.updateMessageText(message_id, newMessageText);
    
            if (updated) {
                Message updatedMessage = accountService.getMessageById(message_id);
                if (updatedMessage != null) {
                    ctx.json(updatedMessage).status(200);
                } else {
                    ctx.status(400);  // Unable to retrieve updated message
                }
            } else {
                ctx.status(400);  // Message update failed
            }
        } else {
            ctx.status(400);  // Invalid input
        }
    }  
    private void getMessagesByUserHandler(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> userMessages = accountService.getMessagesByUser(accountId);
    
        ctx.status(200);
        ctx.json(userMessages);
    }
    
}
    
