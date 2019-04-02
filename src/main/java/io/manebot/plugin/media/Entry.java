package io.manebot.plugin.media;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegError;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import com.github.manevolent.ffmpeg4j.FFmpegIO;
import io.manebot.command.CommandSender;
import io.manebot.command.exception.CommandArgumentException;
import io.manebot.command.exception.CommandExecutionException;
import io.manebot.command.executor.chained.AnnotatedCommandExecutor;
import io.manebot.command.executor.chained.argument.CommandArgumentLabel;
import io.manebot.command.executor.chained.argument.CommandArgumentString;
import io.manebot.plugin.Plugin;
import io.manebot.plugin.PluginException;
import io.manebot.plugin.PluginType;
import io.manebot.plugin.java.PluginEntry;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avformat;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacpp.swresample;

import static org.bytedeco.javacpp.Pointer.*;
import static org.bytedeco.javacpp.Pointer.formatBytes;

public class Entry implements PluginEntry {
    static {
        try {
            FFmpeg.register();
        } catch (FFmpegException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Plugin instantiate(Plugin.Builder builder) throws PluginException {
        return builder
                .type(PluginType.DEPENDENCY)
                .command("media", new AnnotatedCommandExecutor() {
                    @Command(description = "Gets media subsystem version", permission = "media.version")
                    public void version(CommandSender sender,
                                        @CommandArgumentLabel.Argument(label = "version") String version) {
                        sender.sendMessage("Media version information:");
                        sender.sendMessage(" avformat: " + avformat.avformat_version());
                        sender.sendMessage(" avcodec: " + avcodec.avcodec_version());
                        sender.sendMessage(" avutil: " + avutil.avutil_version());
                        sender.sendMessage(" swresample: " + swresample.swresample_version());
                    }

                    @Command(description = "Gets media container information", permission = "media.format.info")
                    public void formatInfo(CommandSender sender,
                                        @CommandArgumentLabel.Argument(label = "info") String info,
                                        @CommandArgumentLabel.Argument(label = "format") String format,
                                        @CommandArgumentString.Argument(label = "name") String name)
                            throws CommandArgumentException {
                        avformat.AVInputFormat inputFormat = avformat.av_find_input_format(name);
                        if (inputFormat == null)
                            throw new CommandArgumentException("Unknown format.");

                        sender.sendMessage("Format " + inputFormat.name().getString() + " information:");
                        sender.sendMessage(" Name: " + inputFormat.long_name().getString());
                    }

                    @Command(description = "Gets media container information", permission = "media.decoder.info")
                    public void decoderInfo(CommandSender sender,
                                        @CommandArgumentLabel.Argument(label = "info") String info,
                                        @CommandArgumentLabel.Argument(label = "decoder") String format,
                                        @CommandArgumentString.Argument(label = "name") String name)
                            throws CommandExecutionException {
                        avcodec.AVCodec codec;

                        try {
                            codec = FFmpeg.getCodecByName(name);
                        } catch (FFmpegException e) {
                            throw new CommandExecutionException(e);
                        }

                        sender.sendMessage("Codec " + codec.name().getString() + " information:");
                        sender.sendMessage(" ID: " + codec.id());
                        sender.sendMessage(" Name: " + codec.long_name().getString());
                    }

                    @Command(description = "Gets media subsystem status", permission = "media.status")
                    public void memoryStatus(CommandSender sender,
                                            @CommandArgumentLabel.Argument(label = "memory-status") String status)
                            throws CommandExecutionException {
                        long physicalBytes = physicalBytes(), // physical memory currently used by the whole process
                                totalBytes = totalBytes(), // current amount of memory tracked by deallocators
                                maxPhysicalBytes = maxPhysicalBytes();

                        double totalPercentage = (double)physicalBytes / (double)maxPhysicalBytes;
                        double nativePercentage = (double)totalBytes / (double)maxPhysicalBytes;

                        sender.sendMessage("Memory status:");

                        sender.sendMessage(" Total memory: " + formatBytes(physicalBytes)
                                + " (" + String.format("%.2f", totalPercentage * 100D) + "%)");

                        sender.sendMessage(" Native memory: " + formatBytes(totalBytes)
                                + " (" + String.format("%.2f", nativePercentage * 100D) + "%)");

                        sender.sendMessage(" Max. physical memory: " + formatBytes(maxPhysicalBytes));
                    }
                })
                .onEnable((future) -> {
                    try {
                        FFmpegError.checkError("avformat_network_init", avformat.avformat_network_init());
                    } catch (FFmpegException e) {
                        throw new PluginException(e);
                    }
                }).onDisable((future) -> {
                    try {
                        FFmpegError.checkError("avformat_network_deinit", avformat.avformat_network_deinit());
                    } catch (FFmpegException e) {
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

    @Override
    public void destruct(Plugin plugin) {

    }
}