package tr.kisisel.vpn

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/** Hızlı Ayarlar paneline eklenen aç/kapat karosu. */
class Karo : TileService() {

    override fun onStartListening() = guncelle()

    override fun onClick() {
        if (VpnService.prepare(this) != null || Depo.oku(this) == null) {
            // İzin veya yapılandırma eksik: uygulamayı aç
            val i = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= 34) {
                startActivityAndCollapse(PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE))
            } else {
                @Suppress("DEPRECATION") startActivityAndCollapse(i)
            }
            return
        }
        Thread {
            try {
                if (VpnYonetici.acikMi(this)) VpnYonetici.kapat(this) else VpnYonetici.ac(this)
            } catch (e: Exception) { }
            Handler(Looper.getMainLooper()).post { guncelle() }
        }.start()
    }

    private fun guncelle() {
        val t = qsTile ?: return
        t.label = "Kişisel VPN"
        t.state = if (VpnYonetici.acikMi(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        t.updateTile()
    }
}
