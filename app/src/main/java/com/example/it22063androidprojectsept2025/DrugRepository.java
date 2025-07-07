package com.example.it22063androidprojectsept2025;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;

import java.util.List;

public class DrugRepository {
    DrugDao drugDao;

    DrugRepository(Application application){
        DrugDatabase db = DrugDatabase.getDatabase(application);
        drugDao = db.drugDao();
    }

    public LiveData<Drug> getDrugById(int id) {
        return drugDao.getDrugByIdLive(id);
    }

    public void deleteById(int id){
         drugDao.deleteById(id);
    }
    public LiveData<List<Drug>> getActiveDrugsOrderedByTimeTerm() {
        return drugDao.getActiveDrugsOrderedByTimeTerm();
    }

    public void deleteAll(){
        new DeleteAllAsyncTask(drugDao).execute();
    }
    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private DrugDao dao;

        DeleteAllAsyncTask(DrugDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAll();
            return null;
        }
    }
    LiveData<List<Drug>> getDrug(){
        return drugDao.getAllDrugs();
    }

    void insert(Drug drug){
        new insertAsyncTask(drugDao).execute(drug);
    }

    private static class insertAsyncTask extends AsyncTask<Drug,Void,Void>{
        private DrugDao taskDao;

        insertAsyncTask(DrugDao drugDao){
            taskDao = drugDao;
        }

        @Override
        protected Void doInBackground(Drug...drugs){
            taskDao.insertAll(drugs[0]);
            return null;
        }


    }
}
