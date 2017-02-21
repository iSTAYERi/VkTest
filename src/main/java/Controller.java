import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.friends.responses.GetResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public static final int VK_APP_ID = 5878777;
    public static final String VK_APP_SECRET = "YoLguNDh64jdd1PtqSMG";
    public static final String VK_APP_SCOPE = "friends";
    public static final String VK_AUTH_URL = "https://oauth.vk.com/authorize?client_id=" +
            VK_APP_ID +
            "&display=page&scope=" +
            VK_APP_SCOPE +
            "&response_type=code&v=5.62";
    public static final String VK_SUCCESS_IDENTIFICATOR = "blank.html#", VK_FAILURE_IDENTIFICATOR = "blank.html#error";


    public Button btnStart;
    public WebView webView;
    public WebEngine webEngine;
    public Button btnGetFriends;
    public UserActor user;

    private boolean loginSuccess = false, loginFailure = false;
    String accessToken, accessCode;
    TransportClient transportClient;
    VkApiClient vk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED){
                changeState(webEngine.getLocation());
            }
        });
        webEngine.load(VK_AUTH_URL);
    }

    private void changeState(String url){
        if(url.contains(VK_FAILURE_IDENTIFICATOR)){
            loginFailure = true;
        }
        else if (url.contains(VK_SUCCESS_IDENTIFICATOR)){
            loginFailure = false;
            loginSuccess = true;
            try{
                getToken(url);
            }catch (Exception e){
                System.out.println("access token is not received");
            }
            try{
                getCode(url);
            }catch (Exception e){
                System.out.println("access code is not received");
            }


            System.out.println(url);
        }
    }

    private void getToken(String url){
        String[] part = url.split("token=");
        String[] part2 = part[1].split("&");
        accessToken = part2[0];
        System.out.println("Access Token: " + accessToken);
    }

    private void getCode(String url){
        String[] part = url.split("code=");
        String[] part2 = part[1].split("&");
        accessCode = part2[0];
        System.out.println("Access Code: " + accessCode);
        authorizationUser();
    }

    private void authorizationUser(){
        try {
            UserAuthResponse authResponse = vk.oauth().userAuthorizationCodeFlow(VK_APP_ID, VK_APP_SECRET, null, accessCode).execute();
            user = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public void onActionBtnStart(ActionEvent actionEvent) {

    }

    public void onActionBtnGetFriends(ActionEvent actionEvent) {
        try {
            GetResponse responseFriends = vk.friends().get(user).execute();
            List<Integer> friends = responseFriends.getItems();
            System.out.println("На аккаунте " + responseFriends.getCount() + " друзей: ");
            System.out.println(responseFriends);
            List<UserXtrCounters> responseUserData = vk.users().get().userIds(String.valueOf(friends)).execute();
            for(UserXtrCounters user : responseUserData){
                System.out.println(user.getFirstName() + " " + user.getLastName());
            }

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
