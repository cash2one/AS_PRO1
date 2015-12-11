/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\as_workspace\\AS_PRO1\\educloud_android_sh\\src\\com\\linkage\\mobile72\\sh\\im\\service\\IChatService.aidl
 */
package com.linkage.mobile72.sh.im.service;
public interface IChatService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.linkage.mobile72.sh.im.service.IChatService
{
private static final java.lang.String DESCRIPTOR = "com.linkage.mobile72.sh.im.service.IChatService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.linkage.mobile72.sh.im.service.IChatService interface,
 * generating a proxy if needed.
 */
public static com.linkage.mobile72.sh.im.service.IChatService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.linkage.mobile72.sh.im.service.IChatService))) {
return ((com.linkage.mobile72.sh.im.service.IChatService)iin);
}
return new com.linkage.mobile72.sh.im.service.IChatService.Stub.Proxy(obj);
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
case TRANSACTION_login:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
long _arg2;
_arg2 = data.readLong();
this.login(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_logout:
{
data.enforceInterface(DESCRIPTOR);
this.logout();
reply.writeNoException();
return true;
}
case TRANSACTION_send:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.send(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_sendPicFile:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
long _arg3;
_arg3 = data.readLong();
java.lang.String _arg4;
_arg4 = data.readString();
this.sendPicFile(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_sendVoiceFile:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
long _arg4;
_arg4 = data.readLong();
java.lang.String _arg5;
_arg5 = data.readString();
this.sendVoiceFile(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_setActiveBuddyId:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _arg1;
_arg1 = data.readInt();
this.setActiveBuddyId(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_registerUploadListener:
{
data.enforceInterface(DESCRIPTOR);
com.linkage.mobile72.sh.im.IUploadFileListener _arg0;
_arg0 = com.linkage.mobile72.sh.im.IUploadFileListener.Stub.asInterface(data.readStrongBinder());
this.registerUploadListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterUploadListener:
{
data.enforceInterface(DESCRIPTOR);
com.linkage.mobile72.sh.im.IUploadFileListener _arg0;
_arg0 = com.linkage.mobile72.sh.im.IUploadFileListener.Stub.asInterface(data.readStrongBinder());
this.unregisterUploadListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_ready:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.ready();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getNotifyStatus:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.getNotifyStatus(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setNotifyStatus:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _arg2;
_arg2 = (0!=data.readInt());
this.setNotifyStatus(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.linkage.mobile72.sh.im.service.IChatService
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
@Override public void login(java.lang.String token, java.lang.String name, long id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
_data.writeString(name);
_data.writeLong(id);
mRemote.transact(Stub.TRANSACTION_login, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void logout() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_logout, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void send(java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void sendPicFile(long toId, java.lang.String filePath, int chattype, long id, java.lang.String groupName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(toId);
_data.writeString(filePath);
_data.writeInt(chattype);
_data.writeLong(id);
_data.writeString(groupName);
mRemote.transact(Stub.TRANSACTION_sendPicFile, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void sendVoiceFile(long toId, java.lang.String filePath, int chattype, int time, long id, java.lang.String groupName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(toId);
_data.writeString(filePath);
_data.writeInt(chattype);
_data.writeInt(time);
_data.writeLong(id);
_data.writeString(groupName);
mRemote.transact(Stub.TRANSACTION_sendVoiceFile, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setActiveBuddyId(long buddyId, int chattype) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(buddyId);
_data.writeInt(chattype);
mRemote.transact(Stub.TRANSACTION_setActiveBuddyId, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerUploadListener(com.linkage.mobile72.sh.im.IUploadFileListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerUploadListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterUploadListener(com.linkage.mobile72.sh.im.IUploadFileListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterUploadListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean ready() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_ready, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getNotifyStatus(long userId, java.lang.String key) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(userId);
_data.writeString(key);
mRemote.transact(Stub.TRANSACTION_getNotifyStatus, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setNotifyStatus(long userId, java.lang.String key, boolean opened) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(userId);
_data.writeString(key);
_data.writeInt(((opened)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setNotifyStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_login = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_logout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_send = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_sendPicFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_sendVoiceFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setActiveBuddyId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_registerUploadListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_unregisterUploadListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_ready = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getNotifyStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_setNotifyStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
public void login(java.lang.String token, java.lang.String name, long id) throws android.os.RemoteException;
public void logout() throws android.os.RemoteException;
public void send(java.lang.String message) throws android.os.RemoteException;
public void sendPicFile(long toId, java.lang.String filePath, int chattype, long id, java.lang.String groupName) throws android.os.RemoteException;
public void sendVoiceFile(long toId, java.lang.String filePath, int chattype, int time, long id, java.lang.String groupName) throws android.os.RemoteException;
public void setActiveBuddyId(long buddyId, int chattype) throws android.os.RemoteException;
public void registerUploadListener(com.linkage.mobile72.sh.im.IUploadFileListener listener) throws android.os.RemoteException;
public void unregisterUploadListener(com.linkage.mobile72.sh.im.IUploadFileListener listener) throws android.os.RemoteException;
public boolean ready() throws android.os.RemoteException;
public boolean getNotifyStatus(long userId, java.lang.String key) throws android.os.RemoteException;
public void setNotifyStatus(long userId, java.lang.String key, boolean opened) throws android.os.RemoteException;
}
