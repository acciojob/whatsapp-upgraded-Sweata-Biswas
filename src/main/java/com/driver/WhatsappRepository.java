package com.driver;

import java.util.*;

public class WhatsappRepository {


//    hashmap of user name and user object
    HashMap<String,User> userHashMap = new HashMap<>();
//    hashmap of group name and group object
    HashMap<String,Group> groupHashMap = new HashMap<>();
//    hashmap of message id and message object
    HashMap<Integer,Message> messageHashMap = new HashMap<>();
//    hashmap of group name and user mobile number who will be the admin
    HashMap<String,String> adminHashMap = new HashMap<>();
//    hashmap of group name and users list
    HashMap<String,List<User>> groupUserHashMap = new HashMap<>();
//    hashmap of user mobileno and list of Messages
    HashMap<String,List<Message>> userMessageHashMap = new HashMap<>();
    //    hashmap of group name and list of Messages
    HashMap<String,List<Message>> groupMessageHashMap= new HashMap<>();
    public String createUser(String name, String mobile) throws Exception{
        if(userHashMap.containsKey(mobile)){
            throw  new Exception("User already exists");
        }
       userHashMap.put(mobile,new User(name,mobile) );
        return "SUCCESS";
    }


    public Group createGroup(List<User> users) {
        String name;
        int numberOfParticipants;
        int count =1;

        if(users.size() ==2){
            name = users.get(1).getName();
            numberOfParticipants= users.size();
        }else{
            numberOfParticipants = users.size();
            for(Map.Entry<String,Group> map  : groupHashMap.entrySet()){
                String key = map.getKey();
               if( key.contains("Group")){
                   count++;
               }
            }
            name = "Group "+ count;
        }

        Group group = new Group(name,numberOfParticipants);
        groupHashMap.put(name, group);
        groupUserHashMap.put(name,users);
        adminHashMap.put(name,users.get(0).getMobile());
        return group;
    }

    public int createMessage(String content) {
        int id = messageHashMap.size() + 1;

        Message message = new Message(id,content);
        messageHashMap.put(id,message);
        return id;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupHashMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }
       List<User> userList = groupUserHashMap.get(group.getName());
       if (!userList.contains(sender)){
           throw new Exception("You are not allowed to send message");
       }

        List<Message> groupMessages = new ArrayList<>();
       if(groupMessageHashMap.containsKey(group.getName())){
           groupMessages = groupMessageHashMap.get(group.getName());
       }

        groupMessages.add(message);

       groupMessageHashMap.put(group.getName(),groupMessages);
        List<Message> userMessages = new ArrayList<>();
        if(userMessageHashMap.containsKey(sender.getMobile())){
            userMessages = userMessageHashMap.get(sender.getMobile());
        }
        userMessages.add(message);
        userMessageHashMap.put(sender.getMobile(),userMessages);
        messageHashMap.put(message.getId(),message);
        return groupMessages.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {

        if(!groupHashMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }
        if(!adminHashMap.containsValue(approver.getMobile())){
            throw new Exception("Approver does not have rights");
        }
        List<User> userList = groupUserHashMap.get(group.getName());
        if (!userList.contains(user)){
            throw new Exception("User is not a participant");
        }
        adminHashMap.put(group.getName(),user.getMobile());
        return "SUCCESS";
    }
    public int removeUser(User user) throws Exception{
        boolean foundUser = false;
        String groupName = null;
        int groupSize = 0;
        int messageCount = 0;
        int overallMessageCount = messageHashMap.size();
       for(Map.Entry<String ,List<User>> map : groupUserHashMap.entrySet()){
          if( map.getValue().contains(user)){
                foundUser = true;
              groupName = map.getKey();
              map.getValue().remove(user);
              groupSize = map.getValue().size();
              break;
           }
       }
       if(!foundUser){
           throw new Exception("User not found");
       }
       if(adminHashMap.containsValue(user.getMobile())){
           throw new Exception("Cannot remove admin");
       }
        userHashMap.remove(user.getMobile());


      Group group = groupHashMap.get(groupName);
        group.setNumberOfParticipants(group.getNumberOfParticipants()-1);


//       List<Message> UserMessageList= userMessageHashMap.get(user.getMobile());
//        List<Message> groupMessageList=groupMessageHashMap.get(group.getName());
//
//        for(Message message: UserMessageList){
//            messageHashMap.remove(message.getId());
//        }
//        groupMessageList.removeAll(UserMessageList);
//        userMessageHashMap.remove(user.getMobile());

        if (userMessageHashMap.containsKey(user.getMobile()))
        {
            messageCount = userMessageHashMap.get(user.getMobile()).size() - 2;
            userMessageHashMap.remove(user.getMobile());
        }
//        return groupSize+groupMessageHashMap.get(groupName).size()+messageHashMap.size();
        return groupSize + messageCount + overallMessageCount;
    }

    public String findMessage(Date start, Date end, int k) throws Exception {
        int messageCount =0;
        for(Map.Entry<Integer, Message> map : messageHashMap.entrySet()){
            if(map.getValue().getTimestamp().after(start) && map.getValue().getTimestamp().before(end)){
                messageCount++;
            }
        }
        if(k > messageCount){
            throw new Exception("K is greater than the number of messages");
        }
        return "SUCCESS";
    }
}
