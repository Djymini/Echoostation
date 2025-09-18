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

    @Query("UPDATE music_tag SET good_vibe_music = :addToMix WHERE id = :id")
    void updateGoodVibeTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET motivation_music = :addToMix WHERE id = :id")
    void updateMotivationTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET party_music = :addToMix WHERE id = :id")
    void updatePartyTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET chill_music = :addToMix WHERE id = :id")
    void updateChillTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET night_music = :addToMix WHERE id = :id")
    void updateNightTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET sad_music = :addToMix WHERE id = :id")
    void updateSadTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET gaming_music = :addToMix WHERE id = :id")
    void updateGamingTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET morning_music = :addToMix WHERE id = :id")
    void updateMorningTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET walk_music = :addToMix WHERE id = :id")
    void updateWalkTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET drive_music = :addToMix WHERE id = :id")
    void updateDriveTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET work_music = :addToMix WHERE id = :id")
    void updateWorkTag(long id, boolean addToMix);

    @Query("UPDATE music_tag SET mind_music = :addToMix WHERE id = :id")
    void updateMindTag(long id, boolean addToMix);

    @Insert
    void insertAll(MusicTag... musicTags);

    @Delete
    void delete(MusicTag musicTag);
}