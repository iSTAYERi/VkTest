import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button btnStart;
    public WebView webView;
    public WebEngine webEngine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);

        webEngine = webView.getEngine();

//        UserAuthResponse authResponse = vk.oauth().userAuthorizationCodeFlow().execute();
    }

    public void onActionBtnStart(ActionEvent actionEvent) {
        webEngine.load("https://oauth.vk.com/authorize?client_id=5878777 &display=page&scope=friends&response_type=code&v=5.62");
        System.out.println(webEngine.getLocation());
    }

}
