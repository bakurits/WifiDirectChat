package com.example.wifidirectchat.connection;


/**
 * ეს ინტერფეისი მოიცავს client და server კლასებს რომელიც თრედებია
 * და რანში კითხულობენ სოკეტიდან ხოლო სენდით აგზავნიან
 */
public abstract class IMessenger extends Thread {
    public abstract void send(String text, boolean isMessage);

    public abstract void DestroySocket();
}
