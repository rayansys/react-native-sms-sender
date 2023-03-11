//
//  SendSMS.h
//  SendSMS
//
//  Created by AlirezaYusefi.
//

#import "RCTBridgeModule.h"
#import <MessageUI/MessageUI.h>

@interface SendSMS : NSObject <MFMessageComposeViewControllerDelegate, RCTBridgeModule> {
    RCTResponseSenderBlock _callback;
}

@end
