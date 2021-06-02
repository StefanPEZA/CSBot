package com.bot.data;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ManagerEntity {
    private static EntityManagerFactory em = null;

    public static EntityManagerFactory getEm(){
        if (em == null)
        {
            em = Persistence.createEntityManagerFactory("default");
            return em;
        }

        return em;
    }
}
