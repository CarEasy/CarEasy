package com.carona.careasy.careasy.activity.helper;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.carona.careasy.careasy.activity.activity.LoginActivity;
import com.carona.careasy.careasy.activity.activity.PassageiroActivity;
import com.carona.careasy.careasy.activity.activity.RequisicoesActivity;
import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setId(firebaseUser.getUid());
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        return usuario;
    }

    public static boolean atualizarNomeUsuario(String nome){

        try {
            FirebaseUser  user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar nome de perfil.");
                    }
                }
            });

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static void redirecionaUsuarioLogado(final Activity activity){

        FirebaseUser user = getUsuarioAtual();
        if(user != null ){
            Intent i = new Intent(activity, PassageiroActivity.class);
            activity.startActivity(i);

        }
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}
