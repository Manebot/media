package io.manebot.plugin.media.filter.audio;

import com.github.manevolent.ffmpeg4j.AudioFrame;
import com.github.manevolent.ffmpeg4j.filter.MediaFilter;
import com.github.manevolent.ffmpeg4j.filter.MediaFilterChain;

import java.util.Collection;

public class AudioFilterChain extends MediaFilterChain<AudioFrame> {
    public AudioFilterChain(Collection<MediaFilter<AudioFrame>> mediaFilters) {
        super(mediaFilters);
    }
}
