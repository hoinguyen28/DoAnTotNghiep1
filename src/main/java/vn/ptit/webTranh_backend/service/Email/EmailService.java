package vn.ptit.webTranh_backend.service.Email;

public interface EmailService {
    public void sendMessage(String from, String to, String subject, String message);
}
