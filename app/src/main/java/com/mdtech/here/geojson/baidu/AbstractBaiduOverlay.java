/*
 * Copyright (C) 2016 The Here Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdtech.here.geojson.baidu;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mdtech.geojson.Position;
import com.mdtech.here.geojson.AbstractGeoJSONOverlay;

import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/15/2016.
 */
public abstract class AbstractBaiduOverlay extends AbstractGeoJSONOverlay {

    protected BaiduMap map;
    // bounds
    private LatLngBounds.Builder boundsBuilder;

    // 将GPS设备采集的原始GPS坐标转换成百度坐标
    static CoordinateConverter converter  = new CoordinateConverter();

    static {
        // 设置坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
    }

    public void addTo(BaiduMap map) {
        this.map = map;
        this.execute();
    }

    @Override
    public void addTo() {
        addTo(map);
    }

    @Override
    public void fitBounds() {
        if(null != boundsBuilder && null != map) {
            map.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(boundsBuilder.build()));
        }
    }

    protected void addToBounds(LatLng latLng) {
        if(null == boundsBuilder) {
            boundsBuilder = new LatLngBounds.Builder();
        }
        boundsBuilder.include(latLng);
    }

    protected void addToBounds(List<LatLng> latLngs) {
        Iterator<LatLng> iterator = latLngs.iterator();
        while (iterator.hasNext()) {
            addToBounds(iterator.next());
        }
    }

    protected void addPositionsTo(List<Position> positions, List<LatLng> latLngs) {
        Iterator<Position> iterator = positions.iterator();
        Position position;
        while (iterator.hasNext()) {
            position = iterator.next();
            latLngs.add(covert(position));
        }
    }

    /**
     * 点转化成坐标对象
     * @param position
     * @return
     */
    protected LatLng covert(Position position) {
        return converter.coord(new LatLng(position.getLatitude(), position.getLongitude()))
                .convert();
    }
}
