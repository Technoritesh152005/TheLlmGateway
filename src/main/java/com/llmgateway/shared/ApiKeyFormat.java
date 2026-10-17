

public final class ApiKeyFormat{

    public static final String Prefix = "sk-gw";
    public static final int SECRET_LENGTH = 43;
    public static final int TOTAL_LENGTH = Prefix.length() + SECRET_LENGTH;

    public static final int Display_Characters = 8;

    //Pattern is a class which helps to identify regex
    public static final Pattern WELL_FORMED = Pattern.compile("^" + Pattern.quote(PREFIX) + "[0-9A-Za-z]{" + SECRET_LENGTH + "}$");

    //private means other class cannot call this means cannot create object of it
    //cause to access static method u dont need obj
    //its only that nnecesaary dont try to create object of it
    private ApiKeyFormat(){}

    public static Boolean isWellFormed(String key){
        return key != null && key.length()==TOTAL_LENGTH && WELL_FORMED.matcher(key).matches();
    }

    //converts the apikey in hash / hex format
    public static String sha256hex(String key){
        try{
            //Messagedigest is a class which provides getInstance to get a object to calculatehash4
            MessageDigest digest = MessageDigest.getInstance("sha256");
            //takes bytes from key and convert in hash later in hexadecimal
            return HexFormat.of().formatHex(digest.digest(key.getBytes(StandardCharsets.UTF_8)))
        }catch(NoSuchAlgorithmException e ){
            throw new IllegalStateException("SHA-256 is guaranteed by the Java platform", e);
        }
    }

}