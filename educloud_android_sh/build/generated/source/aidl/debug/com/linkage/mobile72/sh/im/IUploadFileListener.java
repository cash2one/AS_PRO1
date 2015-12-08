/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\android_studio_workspace\\Web\\educloud_android_sh\\src\\com\\linkage\\mobile72\\sh\\im\\IUploadFileListener.aidl
 */
package com.linkage.mobile72.sh.im;
public interface IUploadFileListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.linkage.mobile72.sh.im.IUploadFileListener
{
private static final java.lang.String DESCRIPTOR = "com.linkage.mobile72.sh.im.IUploadFileListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.linkage.mobile72.sh.im.IUploadFileListener interface,
 * generating a proxy if needed.
 */
public static com.linkage.mobile72.sh.im.IUploadFileListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.linkage.mobile72.sh.im.IUploadFileListener))) {
return ((com.linkage.mobile72.sh.im.IUploadFileListener)iin);
}
return new com.linkage.mobile72.sh.im.IUploadFileListener.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onProgressUpdate:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.onProgressUpdate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onUploadError:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.onUploadError(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onUploadSuccess:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.onUploadSuccess(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.linkage.mobile72.sh.im.IUploadFileListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onProgressUpdate(long id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
mRemote.transact(Stub.TRANSACTION_onProgressUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onUploadError(long id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
mRemote.transact(Stub.TRANSACTION_onUploadError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onUploadSuccess(long id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
mRemote.transact(Stub.TRANSACTION_onUploadSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onProgressUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onUploadError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onUploadSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void onProgressUpdate(long id) throws android.os.RemoteException;
public void onUploadError(long id) throws android.os.RemoteException;
public void onUploadSuccess(long id) throws android.os.RemoteException;
}
