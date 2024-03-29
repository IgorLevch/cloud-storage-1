package com.geekbraines.chat_client.auth;

/**
 * Класс- Singleton для хранения статуса авторизации
 */

public class Authentication {

    public static boolean auth;
    private static Authentication instance;

    private  Authentication(boolean auth){
        Authentication.auth = auth;
    }

    public static Authentication getAuth(boolean auth) {
        if (instance==null) {
            instance = new Authentication(auth);
            return instance;
             } else {
            return instance;
        }
    }
    public static boolean isAuth(){
        return auth;
    }

    public static void setAuth(boolean auth) {
        Authentication.auth = auth;
    }

}
