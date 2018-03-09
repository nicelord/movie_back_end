/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper.gostream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author asus
 */
public class Result {

    @SerializedName("sources")
    @Expose
    private List<Result.Source> sources = null;
    @SerializedName("tracks")
    @Expose
    private List<Result.Track> tracks = null;

    public List<Result.Source> getSources() {
        return sources;
    }

    public void setSources(List<Result.Source> sources) {
        this.sources = sources;
    }

    public List<Result.Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Result.Track> tracks) {
        this.tracks = tracks;
    }

    public class Source {

        @SerializedName("default")
        @Expose
        private Boolean _default;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("file")
        @Expose
        private String file;

        public Boolean getDefault() {
            return _default;
        }

        public void setDefault(Boolean _default) {
            this._default = _default;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

    }

    public class Track {

        @SerializedName("file")
        @Expose
        private String file;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("kind")
        @Expose
        private String kind;
        @SerializedName("default")
        @Expose
        private Boolean _default;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public Boolean getDefault() {
            return _default;
        }

        public void setDefault(Boolean _default) {
            this._default = _default;
        }
    }
}
