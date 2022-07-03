/*  Enigma - Onion Routing based messaging app.
    Copyright (C) 2022  Romulus-Emanuel Ruja <romulus-emanuel.ruja@tutanota.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.example.enigma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContactItem {

    @NonNull
    private String name;

    @NonNull
    private String address;

    @Nullable
    private String additionalInfo;

    @NonNull
    private String sessionId;

    public ContactItem(@NonNull String name, @NonNull String address,
                       @Nullable String additionalInfo,
                       @NonNull String sessionId)
    {
        this.name = name;
        this.address = address;
        this.additionalInfo = additionalInfo;
        this.sessionId = sessionId;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(@Nullable String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
