package com.example.groupproject;

public class Ticket {
    public String ticketStatus, ticketType, ticketMessage;

    public Ticket(){

    }

    public Ticket(String ticketStatus, String ticketType, String ticketMessage){
        this.ticketStatus = ticketStatus;
        this.ticketType = ticketType;
        this.ticketMessage = ticketMessage;
    }
}
