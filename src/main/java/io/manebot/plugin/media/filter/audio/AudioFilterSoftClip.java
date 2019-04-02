package io.manebot.plugin.media.filter.audio;

import com.github.manevolent.ffmpeg4j.AudioFrame;
import com.github.manevolent.ffmpeg4j.filter.audio.AudioFilter;

import java.util.Collection;
import java.util.Collections;

public class AudioFilterSoftClip extends AudioFilter {
    private final SoftClip softClip;

    public AudioFilterSoftClip(int channels) {
        this.softClip = new SoftClip(channels);
    }

    @Override
    public Collection<AudioFrame> apply(AudioFrame source) {
        softClip.process(source.getSamples(), source.getSamples().length);
        return Collections.singletonList(source);
    }

    @Override
    public void close() throws Exception {
        // Do nothing
    }
}
