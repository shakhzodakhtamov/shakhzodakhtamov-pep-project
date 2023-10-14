package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;
//import Controller.SocialMediaController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Account insertAccount(Account account){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "insert into account (username, password) values (?, ?);" ;

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,account.getUsername());
            preparedStatement.setString(2,account.getPassword());
            preparedStatement.executeUpdate();

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_account_id = (int) pkeyResultSet.getLong(1);
                return new Account(generated_account_id, account.getUsername(), account.getPassword());
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    

    public boolean usernameAlreadyExists(String username){
    Connection connection = ConnectionUtil.getConnection();
    try {
        //Write SQL logic here
        String sql = "select * from account where username= ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //write PreparedStatement setString and setInt methods here.
        preparedStatement.setString(1,username);
        ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                return true;
            }
        }catch(SQLException e){
        System.out.println(e.getMessage());
        }
        return false;
    }

    public Account loginAccount (Account account){
        Connection connection = ConnectionUtil.getConnection();
    try {
        //Write SQL logic here
        String sql = "select * from account where username= ? and password= ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,account.getUsername());
            preparedStatement.setString(2,account.getPassword());
        ResultSet rs = preparedStatement.executeQuery();

        while(rs.next()){
            account = new Account(rs.getInt("account_id"), rs.getString("username"),
                    rs.getString("password"));
            return account;
        }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public  Account getUserById(int accound_id){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "select * from account where account_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,accound_id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Account account =new Account(rs.getInt("account_id"), rs.getString("username"),
                rs.getString("password"));
                return account;
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Message addMessage(Message message){
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "insert into Message (posted_by,message_text,time_posted_epoch) values (?, ?, ?);" ;

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,message.getPosted_by());
            preparedStatement.setString(2,message.getMessage_text());
            preparedStatement.setLong(3,message.getTime_posted_epoch());
            preparedStatement.executeUpdate();

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_message_id = (int) pkeyResultSet.getLong(1);
                return new Message(generated_message_id,message.getPosted_by(), message.getMessage_text(),message.getTime_posted_epoch());
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            
            String sql = "select * from message;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"),rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;
    }
    public Message getMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "select * from message where message_id = ?";
    
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
    
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message messageById = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                return messageById;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }    
    public Message deleteMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String selectSql = "SELECT * FROM Message WHERE message_id = ?;";
            String deleteSql = "DELETE FROM Message WHERE message_id = ?;";
    
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, message_id);
            ResultSet rs = selectStatement.executeQuery();
    
            if (rs.next()) {
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");
    
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setInt(1, message_id);
                int deletedRows = deleteStatement.executeUpdate();
    
                if (deletedRows > 0) {
                    return new Message(message_id, posted_by, message_text, time_posted_epoch);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean updateMessageText(int message_id, String newMessageText) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessageText);
            preparedStatement.setInt(2, message_id);
    
            int updatedRows = preparedStatement.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public List<Message> getMessagesByUser(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet rs = preparedStatement.executeQuery();
        
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return messages;
    }    
}
