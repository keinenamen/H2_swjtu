package com.swjtu.h2.custom;

public class Machine_h2 {
        private String mName;
        private String mAddress;
        private String mState;

        public Machine_h2(String name, String address, String state) {
            this.mName = name;
            this.mAddress = address;
            this.mState = state;
        }

        public String getName() {
            return this.mName;
        }

        public String getAdr() {
            return this.mAddress + "";
        }

        public String getSta() {
            return this.mState;
        }
}
