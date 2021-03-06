package nekoqol.utils

import com.google.gson.JsonParser
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import nekoqol.NekoQOL

object UpdateChecker {
    fun hasUpdate(): Int {
        HttpClients.createMinimal().use {
            val httpGet = HttpGet("https://api.github.com/repos/SemiMute/NekoQOL/releases")
            val response = EntityUtils.toString(it.execute(httpGet).entity)
            val version = JsonParser().parse(response).asJsonArray[0].asJsonObject["tag_name"]
            val current = DefaultArtifactVersion(NekoQOL.MOD_VERSION.replace("pre", "beta"))
            val latest = DefaultArtifactVersion(version.asString.replace("pre", "beta"))
            return latest.compareTo(current)
        }
    }
}
