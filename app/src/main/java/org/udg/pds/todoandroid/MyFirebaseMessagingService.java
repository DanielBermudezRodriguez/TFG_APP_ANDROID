package org.udg.pds.todoandroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;

    private String NOTIFICATION_CHANNEL_ID = "my_channel_id_";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        Map<String, String> data = remoteMessage.getData();
        String notificationType = data.get("notificationType");

        if (notificationType != null) {
            switch (notificationType) {
                case "0":
                    mostrarNotificacionEvento(data);
                    break;
                case "1":
                    mostrarNotificacionEvento(data);
                    break;
                case "2":
                    mostrarNotificacionEvento(data);
                    break;
                case "3":
                    mostrarNotificacionEvento(data);
                    break;
                case "4":
                    mostrarNotificacionEvento(data);
                    break;
            }
        }

    }

    private void mostrarNotificacionEvento(final Map<String, String> data) {

        // Obtenemos el identificador del evento cancelado
        String idEvento = data.get("idEvento");
        final String notificationType = data.get("notificationType");
        final boolean esAdministradorEvento = notificationType.equals("3") || notificationType.equals("4");


        Call<Evento> peticionEventos = apiRest.obtenerInformacionEvento(Long.parseLong(idEvento));
        peticionEventos.enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Evento evento = response.body();


                    Intent intent = new Intent(getApplicationContext(), EventoDetalle.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Global.KEY_SELECTED_EVENT, evento);
                    intent.putExtra(Global.KEY_SELECTED_EVENT_IS_ADMIN, esAdministradorEvento);

                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID + notificationType)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_laurel))
                                    .setSmallIcon(R.mipmap.ic_laurel)
                                    .setContentTitle(data.get("title"))
                                    .setContentText(data.get("message"))
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null)
                        notificationManager.notify( Integer.parseInt(notificationType), notificationBuilder.build());

                }
            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });


    }

}
