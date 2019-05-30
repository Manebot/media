import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegIO;
import com.github.manevolent.ffmpeg4j.FFmpegInput;
import junit.framework.TestCase;

import java.io.InputStream;
import java.net.URL;

public class HLSTest extends TestCase {
    public static void main(String[] args) throws Exception {
        new HLSTest().testParser();
    }

    public void testParser() throws Exception {
        FFmpeg.register();

        URL url = new URL("hls+https://manifest.googlevideo.com/api/manifest/hls_playlist/id/VhL4YGTJlBI.1/itag/300/source/yt_live_broadcast/requiressl/yes/ratebypass/yes/live/1/goi/160/sgoap/gir%3Dyes%3Bitag%3D140/sgovp/gir%3Dyes%3Bitag%3D298/hls_chunk_host/r2---sn-vgqsknek.googlevideo.com/playlist_type/DVR/ei/XzrvXN6nG4m3hwbsu4KgCQ/gcr/us/initcwndbps/12590/mm/44/mn/sn-vgqsknek/ms/lva/mv/m/pl/32/dover/11/keepalive/yes/mt/1559181857/disable_polymer/true/ip/2604:6000:8781:5c01:2920:39d:63f:2d8a/ipbits/0/expire/1559203519/sparams/ip,ipbits,expire,id,itag,source,requiressl,ratebypass,live,goi,sgoap,sgovp,hls_chunk_host,playlist_type,ei,gcr,initcwndbps,mm,mn,ms,mv,pl/signature/6BD52BCEC0201221AEA374ABAC4007C9C6110C4D.38D42CB3A418354532BB4C449D140577EDA70D64/key/dg_yt0/playlist/index.m3u8");

        try (InputStream is = url.openConnection().getInputStream()) {
            FFmpegIO io = FFmpegIO.openInputStream(is, FFmpegIO.DEFAULT_BUFFER_SIZE);
            FFmpegInput input = new FFmpegInput(is);
            input.open("hls");
        }
    }
}
