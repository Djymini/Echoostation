package com.djymini.echoostation.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.djymini.echoostation.entities.MusicTag;

import java.util.List;

@Dao
public interface MusicTagDao {
    @Insert
    long insert(MusicTag musicTag);

    @Query("SELECT * FROM music_tag")
    List<MusicTag> getAll();

    @Query("UPDATE music_tag SET favorite_music = :addToMix WHERE id = :id")
    void updateFavoriteTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET happy_music = :addToMix WHERE id = :id")
    void updateHappyTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET motivated_music = :addToMix WHERE id = :id")
    void updateMotivatedTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET sad_music = :addToMix WHERE id = :id")
    void updateSadTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET relaxing_music = :addToMix WHERE id = :id")
    void updateRelaxingTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET introspective_music = :addToMix WHERE id = :id")
    void updateIntrospectiveTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET epic_music = :addToMix WHERE id = :id")
    void updateEpicTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET work_music = :addToMix WHERE id = :id")
    void updateWorkTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET party_music = :addToMix WHERE id = :id")
    void updatePartyTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET ride_music = :addToMix WHERE id = :id")
    void updateRideTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET wake_music = :addToMix WHERE id = :id")
    void updateWakeTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET sleep_music = :addToMix WHERE id = :id")
    void updateSleepTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET wash_music = :addToMix WHERE id = :id")
    void updateWashTag(long id, boolean addToMix);

    @Insert
    void insertAll(MusicTag... musicTags);

    @Delete
    void delete(MusicTag musicTag);
}