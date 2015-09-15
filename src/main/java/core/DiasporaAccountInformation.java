package core;


public class DiasporaAccountInformation {

    public String userName;
    public String password; //password for account Diaspora and for mail
    public String podLink;
    public String email;

    public DiasporaAccountInformation(String userName, String password, String podLink, String email) {
        this.userName = userName;
        this.password = password;
        this.podLink = podLink;
        this.email = email;
    }
}
