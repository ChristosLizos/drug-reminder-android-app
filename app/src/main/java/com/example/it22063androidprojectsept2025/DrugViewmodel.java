package com.example.it22063androidprojectsept2025;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DrugViewmodel extends AndroidViewModel {

    DrugRepository repository;
    LiveData<List<Drug>> drugList;

    public DrugViewmodel(Application application){
        super(application);
        repository = new DrugRepository(application);
        drugList = repository.getDrug();
    }
    public LiveData<List<Drug>> getAllDrugs(){
        return drugList;
    }

    public LiveData<Drug> getDrugById(int id) {
        return repository.getDrugById(id);
    }
    public LiveData<List<Drug>> getActiveDrugsOrderedByTimeTerm() {
        return repository.getActiveDrugsOrderedByTimeTerm();
    }


    public void deleteAllDrugs() {
        repository.deleteAll();
    }

    public void insertDrug(Drug drug){
        repository.insert(drug);
    }
}
