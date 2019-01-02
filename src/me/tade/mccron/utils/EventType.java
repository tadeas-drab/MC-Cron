package me.tade.mccron.utils;

public enum EventType {

    JOIN_EVENT("join-event"),
    QUIT_EVENT("quit-event");

    private String configName;

    EventType(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }

    public static EventType isEventJob(String string){
        for(EventType type : values()){
            if(type.getConfigName().equalsIgnoreCase(string))
                return type;
        }
        return null;
    }
}
