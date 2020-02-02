from Crypto.PublicKey import RSA

def generate_keys(private_file, public_file, bsize=2048):
    key = RSA.generate(bsi)
    private_key = key.export_key()
    file_out = open("private.pem", "wb")
    file_out.write(private_key)

    public_key = key.publickey().export_key()
    file_out = open("receiver.pem", "wb")
    file_out.write(public_key)
    return None

def decrypt(enc_msg):
    dec_msg = 
    return None

if __name__ == "__main__":
    print("this should be doing nothing")