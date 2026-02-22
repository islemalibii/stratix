package org.example;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or

import models.ressource;
import service.service_ressource;

import java.util.List;

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
      //database m = database.getInstance();//
        service_ressource sr = new service_ressource();

        // créer une ressource
        ressource r = new ressource(
                1,
                "Ordinateur",
                "Materiel",
                10,
                "HP"
        );
        ressource r4 = new ressource(
                2,
                "tables",
                "Materiel",
                100,
                "f2"
        );

        // tester l'ajout
        sr.add(r);
        sr.add(r4);




        List<ressource> list = sr.getAll();

        for (ressource rs : list) {
            System.out.println(
                    r.getid() + " | " +
                            r.getNom() + " | " +
                            r.getType_ressource() + " | " +
                            r.getQuatite() + " | " +
                            r.getFournisseur()
            );

        }


        ressource r1 = new ressource(
                1,
                "Ordinateur Portable",
                "Materiel Informatique",
                20,
                "Dell"
        );

        sr.update(r1);



        r.setid(1);   // id à supprimer

        sr.delete(r);



    }








}