package org.aksw.simba.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.carrotsearch.hppc.IntOpenHashSet;

public class ClusterExpStateIO {

    public static void saveClusterExpState(IntOpenHashSet[] clusters, String[] entityIdUriMapping, String filename)
            throws IOException {
        File file = new File(filename);
        File parentFile = file.getAbsoluteFile().getParentFile();
        if ((parentFile != null) && (!parentFile.exists())) {
            parentFile.mkdirs();
        }

        FileOutputStream fout = null;
        ObjectOutputStream oout = null;

        try {
            fout = new FileOutputStream(file);
            oout = new ObjectOutputStream(fout);
            // 1. number of clusters
            oout.writeInt(clusters.length);
            // 2. write the single clusters
            for (int i = 0; i < clusters.length; ++i) {
                oout.writeObject(clusters[i].toArray());
            }
            // 3. write the entityIdUriMapping
            oout.writeObject(entityIdUriMapping);
        } finally {
            if (oout != null) {
                try {
                    oout.close();
                } catch (Exception e) {
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Returns <code>Object[]{IntOpenHashSet[] clusters, String[] entityIdUriMapping}</code>
     * 
     * @param filename
     * @return
     */
    public static Object[] readClusterExpState(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fin = null;
        ObjectInputStream oin = null;
        IntOpenHashSet[] clusters = null;
        String[] entityIdUriMapping = null;
        try {
            fin = new FileInputStream(filename);
            oin = new ObjectInputStream(fin);
            // 1. read the number of clusters
            int numberOfClusters = oin.readInt();
            // 2. read the clusters
            clusters = new IntOpenHashSet[numberOfClusters];
            int temp[];
            for (int i = 0; i < numberOfClusters; ++i) {
                temp = (int[]) oin.readObject();
                clusters[i] = new IntOpenHashSet(temp.length);
                clusters[i].add(temp);
            }
            // 3. read entityIdUriMapping
            entityIdUriMapping = (String[]) oin.readObject();
        } finally {
            if (oin != null) {
                try {
                    oin.close();
                } catch (Exception e) {
                }
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                }
            }
        }
        return new Object[] { clusters, entityIdUriMapping };
    }
}
