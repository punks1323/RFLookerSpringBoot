package com.abcapps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class A {

    public static void main(String[] args) {

        try {
            Path fileStorageLocation = Paths.get("/home/pankaj/Downloads");
            Stream<Path> list = Files.list(fileStorageLocation);


            Map<Date, Path> collect1 = list.collect(Collectors.toMap(path -> {
                try {
                    return new Date(Files.getLastModifiedTime(path).toMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                    return new Date();
                }
            }, path -> path));
            System.out.println(collect1);


            List<Path> listSorted = Files.list(fileStorageLocation).sorted((p1, p2) -> {
                try {
                    return Long.compare(Files.getLastModifiedTime(p1).toMillis(), Files.getLastModifiedTime(p2).toMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;

            }).collect(Collectors.toList());

            System.out.println(listSorted);
            Map<Date, Path> collect2 = listSorted.stream().collect(Collectors.toMap(path -> {
                        try {
                            return new Date(Files.getLastModifiedTime(path).toMillis());
                        } catch (IOException e) {
                            e.printStackTrace();
                            return new Date();
                        }
                    }, path -> path, (u, v) -> {
                        throw new IllegalStateException(String.format("Duplicate key %s", u));
                    },
                    LinkedHashMap::new
            ));

            System.out.println(collect2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
