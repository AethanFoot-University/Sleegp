//
//  module-info
//  eegapp
//
//  Created by soren on 2019-02-22.
//  Copyright © 2019 soren. All rights reserved.
//

module gui {
    requires logic;
    requires javafx.controls;

    opens uk.ac.bath.csed_group_11.gui to javafx.graphics;
    exports uk.ac.bath.csed_group_11.gui;
}