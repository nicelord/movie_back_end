/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.streamangoApi;

import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import com.jaunt.util.HandlerForText;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class RemoteUpload {

    final String API_LOGIN = "uEJHga8wlL";
    final String API_KEY = "tJG8pdYG";
    final String ROOT_FOLDER = "210548";
    final String REMOTE_UPLOAD_ENDPOINT = "http://api.fruithosted.net/remotedl/add?login=uEJHga8wlL&key=tJG8pdYG";
    final String REMOTE_UPLOAD_STATUS = "http://api.fruithosted.net/remotedl/status?login=uEJHga8wlL&key=tJG8pdYG";

    public static void main(String[] args) {
        String remoteUrl = "https://streamango.com/embed/lplbrotlqqroofdb/War_for_the_Planet_of_the_Apes_2017_720p_WEB-DL_XviD_AC3-FGT_mp4";
        RemoteUpload ru = new RemoteUpload();
        System.out.println(ru.checkUploadStatus(ru.addRemoteDl(remoteUrl)));

    }

    public String addRemoteDl(String remoteUrl) {
        String uploadId = "";
        try {
            StringBuilder endPoint = new StringBuilder();
            endPoint.append(REMOTE_UPLOAD_ENDPOINT);
            endPoint.append("&folder=").append(ROOT_FOLDER);
            endPoint.append("&url=").append(remoteUrl);

            UserAgent userAgent = new UserAgent();
            HandlerForText handlerForText = new HandlerForText();
            userAgent.setHandler("application/x-json", handlerForText);

            userAgent.sendGET(endPoint.toString());
            Thread.sleep(500);
            uploadId = userAgent.openJSON(handlerForText.getContent()).findFirst("result").findFirst("id").toString();

        } catch (ResponseException | NotFound | InterruptedException ex) {
            Logger.getLogger(RemoteUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uploadId;
    }

    public String checkUploadStatus(String uploadId) {
        String status = "";
        try {
            StringBuilder endPoint = new StringBuilder();
            endPoint.append(REMOTE_UPLOAD_STATUS);
            endPoint.append("&limit=").append("1");
            endPoint.append("&id=").append(uploadId);

            UserAgent userAgent = new UserAgent();
            HandlerForText handlerForText = new HandlerForText();
            userAgent.setHandler("application/x-json", handlerForText);

            userAgent.sendGET(endPoint.toString());
            Thread.sleep(500);
            status = userAgent.openJSON(handlerForText.getContent()).findFirst(uploadId).findFirst("status").toString();

        } catch (ResponseException | NotFound | InterruptedException ex) {
            Logger.getLogger(RemoteUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
}
