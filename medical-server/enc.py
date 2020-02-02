from Crypto.PublicKey import RSA

def generate_keys(public_file='public.pem',private_file='private.pem', bsize=2048):
    gen_key = RSA.generate(bsize)
    prikey = gen_key.export_key()
    file_out = open(private_file, "wb")
    file_out.write(prikey)

    pubkey = gen_key.publickey().export_key()
    file_out = open(public_file, "wb")
    file_out.write(pubkey)
    return gen_key

def generate_keys_v2(public_file='public.pem',private_file='private.pem', bsize=2048):
    gen_key = RSA.generate(bsize)
    prikey = gen_key.export_key().decode()
    file_out = open(private_file, "w")
    file_out.write(prikey)

    pubkey = gen_key.publickey().export_key().decode()
    file_out = open(public_file, "w")
    file_out.write(pubkey)
    return gen_key

def decrypt(enc_msg, private_key):
    # dec_msg = 
    
    return None

if __name__ == "__main__":
    test_msg = "this is a test message".encode('utf-8')
    print(test_msg)
    bkey = generate_keys('private.pem','public.pem',bsize=1024)
    private_pem = bkey.export_key().decode()
    public_pem = bkey.export_key().decode ()
    print(private_pem)
    print(public_pem)

