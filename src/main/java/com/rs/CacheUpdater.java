package com.rs;

import org.displee.CacheLibrary;
import org.displee.cache.index.archive.Archive;

import java.io.IOException;

/**
 * An {@link CacheUpdater} description here.
 *
 * @author Async <dev.asyncy@gmail.com>
 */
public class CacheUpdater {

    public static void main(String[] args) throws IOException {
        CacheLibrary from = new CacheLibrary("D:\\Downloads\\Mega Downloads\\592 Cache\\cache_592_w_natives\\");
        CacheLibrary to = new CacheLibrary("D:\\RuneScape Private Server\\cache\\");

        int[] maps = {3466, 3469, 3472, 3581, 3583, 3585, 3704, 3706};

        for(int mapas : maps){
            Archive archive = from.getIndex(5).getArchive(mapas);
            to.getIndex(5).addArchive(archive, true, true, archive.getId());
            to.getIndex(5).update();
            System.out.println("Testee");
        }



    }

}
