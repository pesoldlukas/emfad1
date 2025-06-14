package com.emfad.app.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Date,
    val value: Double,
    val unit: String,
    val deviceId: String
)

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    suspend fun getAllMeasurements(): List<Measurement>

    @Insert
    suspend fun insertMeasurement(measurement: Measurement)

    @Query("SELECT * FROM measurements WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun getMeasurementsInRange(startTime: Date, endTime: Date): List<Measurement>
}

@Database(entities = [Measurement::class], version = 1)
abstract class MeasurementDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: MeasurementDatabase? = null

        fun getDatabase(context: android.content.Context): MeasurementDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeasurementDatabase::class.java,
                    "measurement_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
