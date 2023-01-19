package model.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.adapter.LocalDateTimeAdapter;

import java.io.File;
import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager(8078);
        //return new InMemoryTaskManager();
    }



    public static FileBackedTaskManager getDefaultFile() {
        String fileName = "info.csv";
        File fileInfo = new File("resources\\" + fileName);
        return FileBackedTaskManager.loadFromFile(fileInfo);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        //не понимаю, как с адаптером работать.
        // с ним не работает POST-запрос, если оформлять body и отправлять запрос чере постман

        return gsonBuilder.create();
    }
}
