package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    String fileName;

    public AbstractFileRepository(String fileName) {
        super();
        this.fileName = fileName;
        loadData();
    }


    /**
     * reads the data from the file
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                List<String> attr = Arrays.asList(linie.split(";"));
                E e = extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * extract entity  - template method design pattern
     * creates an entity of type E having a specified list of @code attributes
     *
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     * create entity as string - template method design pattern
     * creates an entity as a string
     *
     * @param entity - the given entity
     * @return the entity as a string
     */
    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity) {
        E e = super.save(entity);
        if (e == null) {
            writeToFile(entity);
        }
        return e;

    }

    @Override
    public E delete(ID id) {
        E e = super.delete(id);
        if (e != null) {
            writeToFile();
        }
        return e;
    }

    @Override
    public E update(E entity) {
        E e = super.update(entity);
        if (e == null) {
            writeToFile();
        }
        return e;
    }

    /**
     * writes the data to the file
     */
    protected void writeToFile(E entity) {
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName, true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeToFile() {
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName, false))) {
            List<E> entities = new ArrayList<>();
            super.findAll().forEach(entities::add);
            for (E entity : entities){
                bW.write(createEntityAsString(entity));
                bW.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

