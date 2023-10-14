package Service;

import Model.Account;
import Model.Message;
import DAO.AccountDAO;

import java.util.List;

public class AccountService {
    AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }
    
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }
    public Account addAccount(Account account){
        return accountDAO.insertAccount(account);
    }
    public boolean usernameAlreadyExists(String username) {
        return accountDAO.usernameAlreadyExists(username);
    }
    public Account loginAccount(Account account){
        return accountDAO.loginAccount(account);
    }
    public Account getUserById(int account_id){
        return accountDAO.getUserById(account_id);
    }
    public Message addMessage(Message message){
        return accountDAO.addMessage(message);
    }
    public List<Message> getAllMessages() {
        return accountDAO.getAllMessages();
    }
    /*public Message getMessageById(Message message_id){
        return accountDAO.getMessageById(message_id);
    }*/
    public Message getMessageById(int message_id) {
        return accountDAO.getMessageById(message_id);
    }
    public Message deleteMessageById(int message_id) {
        return accountDAO.deleteMessageById(message_id);
    }
    public boolean updateMessageText(int message_id, String newMessageText) {
        return accountDAO.updateMessageText(message_id, newMessageText);
    }   
    public List<Message> getMessagesByUser(int account_id) {
        return accountDAO.getMessagesByUser(account_id);
    }     
}
