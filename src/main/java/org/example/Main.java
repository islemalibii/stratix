package org.example;

import models.enums.EventStatus;
import models.enums.EventType;
import services.ServiceEvenemnet;
import models.Evenement;
import utils.MyDataBase;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        MyDataBase m = MyDataBase.getInstance();
        Evenement e1 = new Evenement(1,"Jaw","barchajaw", EventType.formation, EventStatus.annuler,"jarda", LocalDate.of(2026, 2, 16));
        Evenement e2 = new Evenement(2,"7aflaa","jawbdhawedds",EventType.recrutement,EventStatus.planifier,"salleM002", LocalDate.of(2026, 2, 16));

        ServiceEvenemnet se = new ServiceEvenemnet();
        //se.add(e1);
        se.update(e2);
        System.out.println(se.getAll());
        System.out.println(se.getAllArchieved());
        int archiver = 1;
        se.archiver(archiver);

    }
}