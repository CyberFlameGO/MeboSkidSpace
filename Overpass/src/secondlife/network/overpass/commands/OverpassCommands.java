package secondlife.network.overpass.commands;

import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.overpass.Overpass;
import secondlife.network.overpass.data.OverpassData;
import secondlife.network.overpass.utilties.OverpassUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by Marko on 22.07.2018.
 */
public class OverpassCommands {

    private static String HOST = Overpass.getInstance().getConfig().getString("SMTP.HOST");
    private static String USERNAME = Overpass.getInstance().getConfig().getString("SMTP.USER");
    private static String PASSWORD = Overpass.getInstance().getConfig().getString("SMTP.PASSWORD");

    @Command(names = {"changepassword", "cp", "changepw"})
    public static void handleChangePW(Player player, @Parameter(name = "oldPassword") String oldPW, @Parameter(name = "newPassword") String newPW) {
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(!overpassData.isRegister() || overpassData.isNeedToEnterCode()) {
            player.sendMessage(Color.translate("&cYou aren't register!"));
            return;
        }

        if(oldPW.equals(newPW)) {
            player.sendMessage(Color.translate("&cYour new password cannot be same as the new one!"));
            return;
        }

        if(!overpassData.getPassword().equals(oldPW)) {
            player.sendMessage(Color.translate("&cInvalid old password!"));
            return;
        }

        if(!OverpassUtils.isPasswordValid(player.getName(), newPW)) {
            player.sendMessage(Color.translate("&cNew password is unsafe!"));
            return;
        }

        overpassData.setPassword(newPW);
        player.kickPlayer(Color.translate("&eYou have successfully changed your password."));
    }

    @Command(names = {"code"})
    public static void handleCode(Player player, @Parameter(name = "code") String code) {
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(!overpassData.isRegister()) {
            player.sendMessage(Color.translate("&cYou aren't register!"));
            return;
        }

        if(!overpassData.isNeedToEnterCode()) {
            player.sendMessage(Color.translate("&cYou don't need to enter the code yet!"));
            return;
        }

        if(!overpassData.getCode().equals(code)) {
            player.sendMessage(Color.translate("&cInvalid code!"));
            return;
        }

        overpassData.setNeedToEnterCode(false);
        overpassData.setNeedLogin(OverpassUtils.isPremium(player) ? false : true);
        overpassData.setFullyRegistered(true);

        player.sendMessage(Color.translate("&eYou have successfully registered."));

        if(overpassData.isNeedLogin()) {
            player.sendMessage(Color.translate("&cPlease login using /login <password>"));
        } else {
            OverpassUtils.sendAuthToBungee(player);
        }

        OverpassUtils.callLoginEvent(player);
    }

    @Command(names = {"login", "l"})
    public static void handleLogin(Player player, @Parameter(name = "password") String password) {
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(OverpassUtils.isPremium(player)) {
            player.sendMessage(Color.translate("&cYou don't need to login!"));
            return;
        }

        if(!overpassData.isFullyRegistered()) {
            player.sendMessage(Color.translate("&cYou aren't register!"));
            return;
        }

        if(!overpassData.getPassword().equals(password)) {
            player.sendMessage(Color.translate("&cInvalid password!"));
            return;
        }

        overpassData.setNeedLogin(false);
        player.sendMessage(Color.translate("&eYou have logged in!"));
        OverpassUtils.callLoginEvent(player);
    }

    @Command(names = {"register", "reg"})
    public static void handleRegister(Player player, @Parameter(name = "password") String password, @Parameter(name = "password") String password2, @Parameter(name = "email") String email) {
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(overpassData.isRegister() || overpassData.isFullyRegistered()) {
            player.sendMessage(Color.translate("&cYou're already registered."));
            return;
        }

        if(!password.equals(password2)) {
            player.sendMessage(Color.translate("&cPlease make sure that both of your passwords are same."));
            return;
        }

        if(password.length() < 5 && password.length() < 25) {
            player.sendMessage(Color.translate("&cYour password must be between 5 and 24 characters!"));
            return;
        }

        if(!OverpassUtils.isPasswordValid(player.getName(), password)) {
            player.sendMessage(Color.translate("&cThat password is unsafe!"));
            return;
        }

        if(!(OverpassUtils.isValidEmailAddress(email))) {
            player.sendMessage(Color.translate("&cPlease specify a valid email address."));
            return;
        }

        if((email.contains("@"))
                || (email.contains("hotmail"))
                || (email.contains("outlook"))
                || (email.contains("gmail"))
                || (email.contains("net"))
                || (email.contains("yahoo"))) {

            Document document = (Document) Vituz.getInstance().getDatabaseManager().getAuthmeProfiles().find(Filters.eq("email", email)).first();

            if(document != null) {
                player.sendMessage(Color.translate("&cThat email is already in use!"));
                return;
            }

            String code = RandomStringUtils.randomNumeric(6);

            overpassData.setRegister(true);
            overpassData.setPassword(password);

            sendEmail(player, email, overpassData, code);

            player.sendMessage(Color.translate("&ePlease check your email at &d" + email + " &eand enter the code to complete your registration."));
        } else {
            player.sendMessage(Color.translate("&cPlease specify a valid email address."));
        }
    }

    public static void sendEmail(Player player, String email, OverpassData overpassData, String code) {
        new BukkitRunnable() {
            public void run() {
                Properties props = new Properties();
                props.put("mail.smtp.host", HOST);
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(USERNAME, PASSWORD);
                            }
                        });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(USERNAME, "SecondLife Network"));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    message.setSubject("Please complete your registration");
                    message.setText("Balkan: \n" +
                            "Hvala na registraciji, medutim kako bi dovrsili registraciju morate potvrditi vas email. \n" +
                            "Kako bi ste dovrsili registraciju morate upisati /code " + code + "\n" +
                            "Hvala, \n" +
                            "SecondLife Team \n\n\n" +
                            "English: \n" +
                            "Thank you for registering, however, in order to complete your registration you must verify your email. \n" +
                            "In order to complete your registration you have to type /code " + code + "\n" +
                            "Thanks,\n" +
                            "SecondLife Team");

                    Transport.send(message);

                    overpassData.setCode(code);
                    overpassData.setNeedToEnterCode(true);
                    overpassData.setEmail(email);

                    if(player != null) {
                        Bukkit.getScheduler().runTask(Overpass.getInstance(), () -> {
                            if(Bukkit.isPrimaryThread()) {
                                player.kickPlayer(Color.translate("&cYou have successfully registered. Please join back and finish the registration!"));
                            } else {
                                System.out.print("Tried async kicking " + player.getName());
                            }
                        });
                    } else {
                        overpassData.save();
                    }

                } catch(MessagingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Overpass.getInstance());
    }
}
