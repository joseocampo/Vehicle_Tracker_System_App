package joseocampo.VehicleTrackerSystemApp.com;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static android.content.ContentValues.TAG;

public class GmailHelper {

    Session session = null;
    Properties prop = null;
    private String Destino, Titulo, Mensaje;
    private static String cryptoPass = "sup3rS3xy";
    public boolean email_enviado;

    public GmailHelper(){
        this.email_enviado = false;
    }


    public boolean sendEmail(String cedula, String vehicle){
        prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "465");


        Destino = "arleyy1004@gmail.com,moiso1908@gmail.com";
        Titulo = "Desactivacion de GPS";
        Mensaje = ContenidoMensaje(cedula,vehicle);


        session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("vehicletrackersystemmsph@gmail.com",ObtenerPermisos());
            }
        });

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
        email_enviado = true;
        return email_enviado;
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("vehicletrackersystemmsph@gmail.com")); /*Correo de la "empresa". */
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Destino)); /*Almacena el correo del destinatario.*/
                message.setSubject(Titulo); /*Almacena el Titulo del mensaje.*/
                message.setContent(Mensaje, "text/html; charset=utf-8"); /*Determina la codificacion del mensaje.*/

                Transport.send(message); /*Sen envia el mensaje*/

            } catch(MessagingException e){
                e.printStackTrace();;
            } catch(Exception e){
                e.printStackTrace();;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            email_enviado = false;
        }
    }

    private String ContenidoMensaje(String cedula, String vehicle){

        return "El usuario con cedula " + cedula + " en el vehicullo " + vehicle + " ha desativado el GPS";
    }



    private String Desencriptar( String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes);
            Log.d(TAG, "Decrypted: " + value + " -> " + decrypedValue);
            return decrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

    private String ObtenerPermisos(){
        String res = "";
        res = Desencriptar("IJzbjS6PUr1+M32GQhlQFA==");
        return res;
    }


}
