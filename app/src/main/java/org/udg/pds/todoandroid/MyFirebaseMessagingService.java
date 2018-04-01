package org.udg.pds.todoandroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.udg.pds.todoandroid.activity.Login;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        Map<String, String> data = remoteMessage.getData();
        String notificationType = data.get("notificationType");

        if (notificationType != null) {
            if (notificationType.equals("0")){
                mostrarNotificacionEventoCancelado(data);
            }
        }
    }

    private void mostrarNotificacionEventoCancelado(Map<String, String> data) {

        String miss = "Missatge enviat per " + data.get("nickname") + ":\n" + data.get("content");

        Intent intent = new Intent(MyFirebaseMessagingService.this, Login.class);
        //intent.putExtra("userRemotId", data.get("idUser"));
        //intent.putExtra("userReceptorId", String.valueOf(SingletonUserInfo.getInstance().getUserID()));
        //intent.putExtra("userRemotNickname", data.get("nickname"));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyFirebaseMessagingService.this);
        stackBuilder.addParentStack(Login.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle(data.get("title"))
                        .setContentText(data.get("message"))
                        .setAutoCancel(true);

        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

}
