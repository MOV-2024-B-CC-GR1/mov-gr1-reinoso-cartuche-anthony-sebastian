import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.security.Guard;

public class ESqliteHelperEntrenador(
    contexto: Context?
): SQLiteOpenHelper(
        contexto,
        "moviles",
                null,
                1
)
{
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int){}
}

