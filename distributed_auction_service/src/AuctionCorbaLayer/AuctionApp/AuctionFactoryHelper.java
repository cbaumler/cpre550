package AuctionApp;


/**
* AuctionApp/AuctionFactoryHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from auction.idl
* Sunday, April 6, 2014 2:45:20 AM CDT
*/

abstract public class AuctionFactoryHelper
{
  private static String  _id = "IDL:AuctionApp/AuctionFactory:1.0";

  public static void insert (org.omg.CORBA.Any a, AuctionApp.AuctionFactory that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static AuctionApp.AuctionFactory extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (AuctionApp.AuctionFactoryHelper.id (), "AuctionFactory");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static AuctionApp.AuctionFactory read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_AuctionFactoryStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, AuctionApp.AuctionFactory value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static AuctionApp.AuctionFactory narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof AuctionApp.AuctionFactory)
      return (AuctionApp.AuctionFactory)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      AuctionApp._AuctionFactoryStub stub = new AuctionApp._AuctionFactoryStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static AuctionApp.AuctionFactory unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof AuctionApp.AuctionFactory)
      return (AuctionApp.AuctionFactory)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      AuctionApp._AuctionFactoryStub stub = new AuctionApp._AuctionFactoryStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
