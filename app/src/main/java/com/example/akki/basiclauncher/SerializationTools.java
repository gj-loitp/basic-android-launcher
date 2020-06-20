package com.example.akki.basiclauncher;

import android.content.Context;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by akshay on 19-10-2017.
 */

public class SerializationTools {

    public static void serializeData(AppSerializableData obj) {
        FileOutputStream fos;

        try {
            fos = MainActivity.activity.openFileOutput("data", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeFavApps(FavoriteApps obj) {
        FileOutputStream fos;

        try {
            fos = MainActivity.activity.openFileOutput("favapps", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppSerializableData loadSerializedData() {
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(MainActivity.activity.openFileInput("data"));
            Object obj = inputStream.readObject();

            if(obj instanceof AppSerializableData) {
                return (AppSerializableData) obj;
            }else {
                return null;
            }

        }catch (EOFException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e) {

            }
        }

        return null;
    }

    public static FavoriteApps loadFavApps() {
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(MainActivity.activity.openFileInput("favapps"));
            Object obj = inputStream.readObject();

            if(obj instanceof FavoriteApps) {
                return (FavoriteApps) obj;
            }else {
                return null;
            }

        }catch (EOFException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e) {

            }
        }

        return null;
    }
}
