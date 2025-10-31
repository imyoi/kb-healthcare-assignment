package com.kb.healthcare.myohui.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class HealthDataRequest {

    @JsonProperty("recordkey")
    private String recordKey;

    private String type;
    private String lastUpdate;
    private Data data;

    @Getter
    public static class Data {
        private List<Entry> entries;
        private Source source;
    }

    @Getter
    public static class Source {
        private int mode;
        private Product product;
        private String name;
        private String type;
    }

    @Getter
    public static class Product {
        private String name;
        private String vender;
    }

    @Getter
    public static class Entry {
        private Period period;
        private Metric distance;
        private Metric calories;
        private int steps;

        @Getter
        public static class Period {
            private String from;
            private String to;
        }

        @Getter
        public static class Metric {
            private String unit;
            private float value;
        }
    }
}