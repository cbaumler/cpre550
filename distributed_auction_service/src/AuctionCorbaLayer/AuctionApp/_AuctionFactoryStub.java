package AuctionApp;


/**
* AuctionApp/_AuctionFactoryStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from auction.idl
* Sunday, April 6, 2014 2:45:20 AM CDT
*/

public class _AuctionFactoryStub extends org.omg.CORBA.portable.ObjectImpl implements AuctionApp.AuctionFactory
{


  // Raises INVALID_USER exception if the userId is in the database, but the password doesn't match
  public void authenticate (String userId, String password) throws AuctionApp.AuctionFailure
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("authenticate", true);
                $out.write_string (userId);
                $out.write_string (password);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:AuctionApp/AuctionFailure:1.0"))
                    throw AuctionApp.AuctionFailureHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                authenticate (userId, password        );
            } finally {
                _releaseReply ($in);
            }
  } // authenticate


  // Raises GENERIC_ERROR if an error occurs when contacting the auction servers
  public void getActiveAuctions (String userId, AuctionApp.AuctionFactoryPackage.AuctionsHolder sellerAuctions, org.omg.CORBA.IntHolder numSellerAuctions, AuctionApp.AuctionFactoryPackage.AuctionsHolder bidderAuctions, org.omg.CORBA.IntHolder numBidderAuctions) throws AuctionApp.AuctionFailure
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getActiveAuctions", true);
                $out.write_string (userId);
                $in = _invoke ($out);
                sellerAuctions.value = AuctionApp.AuctionFactoryPackage.AuctionsHelper.read ($in);
                numSellerAuctions.value = $in.read_ulong ();
                bidderAuctions.value = AuctionApp.AuctionFactoryPackage.AuctionsHelper.read ($in);
                numBidderAuctions.value = $in.read_ulong ();
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:AuctionApp/AuctionFailure:1.0"))
                    throw AuctionApp.AuctionFailureHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                getActiveAuctions (userId, sellerAuctions, numSellerAuctions, bidderAuctions, numBidderAuctions        );
            } finally {
                _releaseReply ($in);
            }
  } // getActiveAuctions


  // Raises GENERIC_ERROR if an error occurs when contacting the auction servers
  public String getInactiveAuction () throws AuctionApp.AuctionFailure
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getInactiveAuction", true);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:AuctionApp/AuctionFailure:1.0"))
                    throw AuctionApp.AuctionFailureHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getInactiveAuction (        );
            } finally {
                _releaseReply ($in);
            }
  } // getInactiveAuction

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:AuctionApp/AuctionFactory:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _AuctionFactoryStub
