package org.nds.dbdroid.remoting;

import org.nds.dbdroid.remoting.commons.entity.Contact;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactParcelable implements Parcelable {

	private Contact contact;

	// Constructor
	public ContactParcelable(Contact contact) {

		this.contact = contact;
	}

	// Parcelling part
	public ContactParcelable(Parcel in) {
		this.contact = (Contact) in.readValue(Contact.class.getClassLoader());
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.contact);
	}

	public static final Parcelable.Creator<ContactParcelable> CREATOR = new Parcelable.Creator<ContactParcelable>() {

		public ContactParcelable createFromParcel(Parcel in) {
			return new ContactParcelable(in);
		}

		public ContactParcelable[] newArray(int size) {
			return new ContactParcelable[size];
		}

	};

}
