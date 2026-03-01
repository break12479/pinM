package com.pinmem.memoryai.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MemoryDatabase_Impl extends MemoryDatabase {
  private volatile MemoryDao _memoryDao;

  private volatile AIConfigDao _aIConfigDao;

  private volatile TagDao _tagDao;

  private volatile QAHistoryDao _qAHistoryDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  private volatile EmbeddingQueueDao _embeddingQueueDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `memories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT NOT NULL, `media_type` TEXT NOT NULL, `media_path` TEXT, `media_description` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `location_lat` REAL, `location_lng` REAL, `location_address` TEXT, `scene_category` TEXT NOT NULL, `type_category` TEXT NOT NULL, `tags` TEXT NOT NULL, `embedding` BLOB, `embedding_model` TEXT, `is_deleted` INTEGER NOT NULL, `deleted_at` INTEGER, `ai_processed` INTEGER NOT NULL, `embedding_pending` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ai_config` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `provider` TEXT NOT NULL, `api_key` TEXT NOT NULL, `base_url` TEXT, `embedding_model` TEXT NOT NULL, `llm_model` TEXT NOT NULL, `is_active` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `embedding` BLOB, `embedding_model` TEXT, `usage_count` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_preferred` INTEGER NOT NULL, `alias_of` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `qa_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `question` TEXT NOT NULL, `answer` TEXT NOT NULL, `referenced_memory_ids` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `feedback` INTEGER, `model_used` TEXT, `tokens_used` INTEGER, `latency_ms` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `query` TEXT NOT NULL, `result_count` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `search_type` TEXT NOT NULL, `filter_tags` TEXT, `filter_category` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `embedding_queue` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `memory_id` INTEGER NOT NULL, `content` TEXT NOT NULL, `retry_count` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `last_attempt_at` INTEGER, `error_message` TEXT, FOREIGN KEY(`memory_id`) REFERENCES `memories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_embedding_queue_memory_id` ON `embedding_queue` (`memory_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_embedding_queue_last_attempt_at` ON `embedding_queue` (`last_attempt_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f075a168c5dacb7ff0317bb35b276d4b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `memories`");
        db.execSQL("DROP TABLE IF EXISTS `ai_config`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `qa_history`");
        db.execSQL("DROP TABLE IF EXISTS `search_history`");
        db.execSQL("DROP TABLE IF EXISTS `embedding_queue`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMemories = new HashMap<String, TableInfo.Column>(19);
        _columnsMemories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("media_type", new TableInfo.Column("media_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("media_path", new TableInfo.Column("media_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("media_description", new TableInfo.Column("media_description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("location_lat", new TableInfo.Column("location_lat", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("location_lng", new TableInfo.Column("location_lng", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("location_address", new TableInfo.Column("location_address", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("scene_category", new TableInfo.Column("scene_category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("type_category", new TableInfo.Column("type_category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("embedding", new TableInfo.Column("embedding", "BLOB", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("embedding_model", new TableInfo.Column("embedding_model", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("is_deleted", new TableInfo.Column("is_deleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("deleted_at", new TableInfo.Column("deleted_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("ai_processed", new TableInfo.Column("ai_processed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemories.put("embedding_pending", new TableInfo.Column("embedding_pending", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMemories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMemories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMemories = new TableInfo("memories", _columnsMemories, _foreignKeysMemories, _indicesMemories);
        final TableInfo _existingMemories = TableInfo.read(db, "memories");
        if (!_infoMemories.equals(_existingMemories)) {
          return new RoomOpenHelper.ValidationResult(false, "memories(com.pinmem.memoryai.data.model.Memory).\n"
                  + " Expected:\n" + _infoMemories + "\n"
                  + " Found:\n" + _existingMemories);
        }
        final HashMap<String, TableInfo.Column> _columnsAiConfig = new HashMap<String, TableInfo.Column>(7);
        _columnsAiConfig.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConfig.put("provider", new TableInfo.Column("provider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConfig.put("api_key", new TableInfo.Column("api_key", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConfig.put("base_url", new TableInfo.Column("base_url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConfig.put("embedding_model", new TableInfo.Column("embedding_model", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConfig.put("llm_model", new TableInfo.Column("llm_model", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConfig.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAiConfig = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAiConfig = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAiConfig = new TableInfo("ai_config", _columnsAiConfig, _foreignKeysAiConfig, _indicesAiConfig);
        final TableInfo _existingAiConfig = TableInfo.read(db, "ai_config");
        if (!_infoAiConfig.equals(_existingAiConfig)) {
          return new RoomOpenHelper.ValidationResult(false, "ai_config(com.pinmem.memoryai.data.model.AIConfig).\n"
                  + " Expected:\n" + _infoAiConfig + "\n"
                  + " Found:\n" + _existingAiConfig);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(9);
        _columnsTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("embedding", new TableInfo.Column("embedding", "BLOB", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("embedding_model", new TableInfo.Column("embedding_model", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("usage_count", new TableInfo.Column("usage_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("is_preferred", new TableInfo.Column("is_preferred", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("alias_of", new TableInfo.Column("alias_of", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.pinmem.memoryai.data.model.Tag).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsQaHistory = new HashMap<String, TableInfo.Column>(9);
        _columnsQaHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("question", new TableInfo.Column("question", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("answer", new TableInfo.Column("answer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("referenced_memory_ids", new TableInfo.Column("referenced_memory_ids", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("feedback", new TableInfo.Column("feedback", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("model_used", new TableInfo.Column("model_used", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("tokens_used", new TableInfo.Column("tokens_used", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaHistory.put("latency_ms", new TableInfo.Column("latency_ms", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQaHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQaHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQaHistory = new TableInfo("qa_history", _columnsQaHistory, _foreignKeysQaHistory, _indicesQaHistory);
        final TableInfo _existingQaHistory = TableInfo.read(db, "qa_history");
        if (!_infoQaHistory.equals(_existingQaHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "qa_history(com.pinmem.memoryai.data.model.QAHistory).\n"
                  + " Expected:\n" + _infoQaHistory + "\n"
                  + " Found:\n" + _existingQaHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchHistory = new HashMap<String, TableInfo.Column>(7);
        _columnsSearchHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("query", new TableInfo.Column("query", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("result_count", new TableInfo.Column("result_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("search_type", new TableInfo.Column("search_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("filter_tags", new TableInfo.Column("filter_tags", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("filter_category", new TableInfo.Column("filter_category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchHistory = new TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory);
        final TableInfo _existingSearchHistory = TableInfo.read(db, "search_history");
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "search_history(com.pinmem.memoryai.data.model.SearchHistory).\n"
                  + " Expected:\n" + _infoSearchHistory + "\n"
                  + " Found:\n" + _existingSearchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsEmbeddingQueue = new HashMap<String, TableInfo.Column>(7);
        _columnsEmbeddingQueue.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmbeddingQueue.put("memory_id", new TableInfo.Column("memory_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmbeddingQueue.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmbeddingQueue.put("retry_count", new TableInfo.Column("retry_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmbeddingQueue.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmbeddingQueue.put("last_attempt_at", new TableInfo.Column("last_attempt_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmbeddingQueue.put("error_message", new TableInfo.Column("error_message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmbeddingQueue = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEmbeddingQueue.add(new TableInfo.ForeignKey("memories", "CASCADE", "NO ACTION", Arrays.asList("memory_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEmbeddingQueue = new HashSet<TableInfo.Index>(2);
        _indicesEmbeddingQueue.add(new TableInfo.Index("index_embedding_queue_memory_id", false, Arrays.asList("memory_id"), Arrays.asList("ASC")));
        _indicesEmbeddingQueue.add(new TableInfo.Index("index_embedding_queue_last_attempt_at", false, Arrays.asList("last_attempt_at"), Arrays.asList("ASC")));
        final TableInfo _infoEmbeddingQueue = new TableInfo("embedding_queue", _columnsEmbeddingQueue, _foreignKeysEmbeddingQueue, _indicesEmbeddingQueue);
        final TableInfo _existingEmbeddingQueue = TableInfo.read(db, "embedding_queue");
        if (!_infoEmbeddingQueue.equals(_existingEmbeddingQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "embedding_queue(com.pinmem.memoryai.data.model.EmbeddingQueue).\n"
                  + " Expected:\n" + _infoEmbeddingQueue + "\n"
                  + " Found:\n" + _existingEmbeddingQueue);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f075a168c5dacb7ff0317bb35b276d4b", "af61761b50bfac6b709f54dd8bfb2d3f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "memories","ai_config","tags","qa_history","search_history","embedding_queue");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `memories`");
      _db.execSQL("DELETE FROM `ai_config`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `qa_history`");
      _db.execSQL("DELETE FROM `search_history`");
      _db.execSQL("DELETE FROM `embedding_queue`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MemoryDao.class, MemoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AIConfigDao.class, AIConfigDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QAHistoryDao.class, QAHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchHistoryDao.class, SearchHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EmbeddingQueueDao.class, EmbeddingQueueDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MemoryDao memoryDao() {
    if (_memoryDao != null) {
      return _memoryDao;
    } else {
      synchronized(this) {
        if(_memoryDao == null) {
          _memoryDao = new MemoryDao_Impl(this);
        }
        return _memoryDao;
      }
    }
  }

  @Override
  public AIConfigDao aiConfigDao() {
    if (_aIConfigDao != null) {
      return _aIConfigDao;
    } else {
      synchronized(this) {
        if(_aIConfigDao == null) {
          _aIConfigDao = new AIConfigDao_Impl(this);
        }
        return _aIConfigDao;
      }
    }
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }

  @Override
  public QAHistoryDao qaHistoryDao() {
    if (_qAHistoryDao != null) {
      return _qAHistoryDao;
    } else {
      synchronized(this) {
        if(_qAHistoryDao == null) {
          _qAHistoryDao = new QAHistoryDao_Impl(this);
        }
        return _qAHistoryDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }

  @Override
  public EmbeddingQueueDao embeddingQueueDao() {
    if (_embeddingQueueDao != null) {
      return _embeddingQueueDao;
    } else {
      synchronized(this) {
        if(_embeddingQueueDao == null) {
          _embeddingQueueDao = new EmbeddingQueueDao_Impl(this);
        }
        return _embeddingQueueDao;
      }
    }
  }
}
