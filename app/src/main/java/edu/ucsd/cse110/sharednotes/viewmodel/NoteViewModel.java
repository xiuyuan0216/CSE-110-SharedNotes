package edu.ucsd.cse110.sharednotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import edu.ucsd.cse110.sharednotes.model.Note;
import edu.ucsd.cse110.sharednotes.model.NoteAPI;
import edu.ucsd.cse110.sharednotes.model.NoteDatabase;
import edu.ucsd.cse110.sharednotes.model.NoteRepository;

public class NoteViewModel extends AndroidViewModel {
    private LiveData<Note> note;
    private final NoteRepository repo;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        var context = application.getApplicationContext();
        var db = NoteDatabase.provide(context);
        var dao = db.getDao();
        this.repo = new NoteRepository(dao);
    }

    public LiveData<Note> getNote(String title) {
        // TODO: use getSynced here instead?
        // The returned live data should update whenever there is a change in
        // the database, or when the server returns a newer version of the note.
        // Polling interval: 3s.
        if (note == null) {
            note = repo.getLocal(title);

            MutableLiveData<Note> change = (MutableLiveData<Note>) note;
            repo.getRemote(title).observeForever(new Observer<Note>() {
                @Override
                public void onChanged(Note n) {
                    change.postValue(n);
                    note = change;
                }
            });



        }
        return note;
    }

    public void save(Note note) {
        // TODO: try to upload the note to the server.

        repo.upsertLocal(note);
        repo.upsertRemote(note);
    }
}
