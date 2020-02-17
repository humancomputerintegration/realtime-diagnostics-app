from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
import base64

def generate_keys(public_file='public.pem',private_file='private.pem', bsize=2048):
    gen_key = RSA.generate(bsize)
    prikey = gen_key.export_key()
    file_out = open(private_file, "wb")
    file_out.write(prikey)

    pubkey = gen_key.publickey().export_key()
    file_out = open(public_file, "wb")
    file_out.write(pubkey)
    return gen_key

def decrypt(enc_msg, pkeyf= 'private.pem'):
    prikey= RSA.import_key(open(pkeyf, 'rb').read())
    decrypter= PKCS1_OAEP.new(key=prikey) 
    dec_msg = decrypt.decrypt(enc_msg)
    return dec_msg 

#The reason for smaller sized keys is for faster encryption/decryption
#in addition to being able to fit under the text limit of SMS 
# however, this comes at the expense of security (not to a serious extent)
def testing(test_strings, write_new_keys=False, bsize=1024):
    print("Testing encryption module with some test_strings")
    print("------------------------------------------------")

    enc_str,ctxts,dmsg = [],[],[]

    for s in test_strings: 
        enc_str.append(s.encode('utf-8'))

    print(enc_str)

    if(write_new_keys):
        bkey = generate_keys('public.pem','private.pem',bsize=bsize)
        prik = RSA.import_key(open("private.pem",'r').read())
        pubk = RSA.import_key(open("public.pem",'r').read())
    else: 
        gen_key = RSA.generate(bsize)
        prik = RSA.import_key(gen_key.export_key())
        pubk = RSA.import_key(gen_key.publickey().export_key())

    # print(private_pem)
    # print(public_pem)
    cipher = PKCS1_OAEP.new(key=pubk)
    for ind,es in enumerate(enc_str):
        tempc = base64.a85encode(cipher.encrypt(es))
        print("encrypted msg length {} = {}".format(ind, len(tempc)))
        ctxts.append(tempc)

    decrypt = PKCS1_OAEP.new(key=prik)
    for ct in ctxts:
        tmpd = decrypt.decrypt(base64.a85decode(ct))
        dmsg.append(tmpd)

    for index,result in enumerate(dmsg): 
        assert result ==  enc_str[index]
    print("------------------------------------------------")
    print("Passed")
    return True

if __name__ == "__main__":
    test_strings = []
    test_strings.append("this is a test message")
    test_strings.append("this is a test message and I am testing if this works")
    test_strings.append('''this is a test message and I am testing if this works \\ 
                with respect''')

    testing(test_strings, write_new_keys = True)

