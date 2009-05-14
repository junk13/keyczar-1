// Copyright 2009 Sebastien Martini (seb@dbzteam.org)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
#ifndef KEYCZAR_OPENSSL_DSA_H_
#define KEYCZAR_OPENSSL_DSA_H_

#include <openssl/dsa.h>
#include <string>

#include "base/basictypes.h"
#include "base/scoped_ptr.h"
#include "testing/gtest/include/gtest/gtest_prod.h"

#include "keyczar/dsa_impl.h"
#include "keyczar/openssl/util.h"

namespace keyczar {

namespace openssl {

// OpenSSL concrete implementation.
class DSAOpenSSL : public DSAImpl {
 public:
  DSAOpenSSL(DSA* key, bool private_key)
      : key_(key), private_key_(private_key) {}

  virtual ~DSAOpenSSL() {}

  // Builds a concrete DSA implementation object from |key|. |key| must be
  // correctly initialized and will be used to instanciate an OpenSSL DSA
  // key. The caller takes ownership over the returned object.
  static DSAOpenSSL* Create(const DSAIntermediateKey& key, bool private_key);

  // Builds a concrete DSA implementation object from a new generated key of
  // length |size| and returns the result to the caller who takes ownership
  // over the returned instance. The value of |size| is expressed in bits.
  static DSAOpenSSL* GenerateKey(int size);

  // Builds a concrete DSA implementation object from the PEM private key stored
  // at |filename|. |passphrase| is the optional passphrase. Pass NULL if there
  // is no passphrase of if it will be asked interactively. The caller takes
  // ownership over the returned object.
  static DSAOpenSSL* CreateFromPEMKey(const std::string& filename,
                                      const std::string* passphrase);

  bool WriteKeyToPEMFile(const std::string& filename);

  virtual bool GetAttributes(DSAIntermediateKey* key);

  virtual bool GetPublicAttributes(DSAIntermediateKey* key);

  virtual bool Sign(const std::string& message_digest,
                    std::string* signature) const;

  virtual bool Verify(const std::string& message_digest,
                      const std::string& signature) const;

  virtual int Size() const;

  bool Equals(const DSAOpenSSL& rhs) const;

  bool private_key() const { return private_key_; }

  const DSA* key() const { return key_.get(); }

 private:
  FRIEND_TEST(DSAOpenSSL, CreateKeyAndCompare);

  typedef scoped_ptr_malloc<
      DSA, openssl::OSSLDestroyer<DSA, DSA_free> > ScopedDSAKey;

  ScopedDSAKey key_;

  bool private_key_;

  DISALLOW_COPY_AND_ASSIGN(DSAOpenSSL);
};

}  // namespace openssl

}  // namespace keyczar

#endif  // KEYCZAR_OPENSSL_DSA_H_
